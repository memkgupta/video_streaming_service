package com.vsnt.asset_onboarding.workers;

import com.vsnt.asset_onboarding.BitrateConfig;

import com.vsnt.asset_onboarding.config.Secrets;
import com.vsnt.asset_onboarding.entities.Media;
import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import com.vsnt.asset_onboarding.services.S3Service;
import com.vsnt.asset_onboarding.strategies.playlist.PlaylistGenerationStrategy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
public class PlaylistGeneratorWorker {
    private final PlaylistGenerationStrategy playlistGenerator;
    private final Executor executor = Executors.newFixedThreadPool(4);
    private final List<BitrateConfig> bitrateConfigs =
            List.of(
                    BitrateConfig.builder()
                            .resolution(ResolutionEnum.RESOLUTION_360P)
                            .build(),//360p
                    BitrateConfig.builder()
                            .resolution(ResolutionEnum.RESOLUTION_480P)
                            .build(),//480p
                    BitrateConfig.builder()
                            .resolution(ResolutionEnum.RESOLUTION_720P)
                            .build(),//720p
                    BitrateConfig.builder()
                            .resolution(ResolutionEnum.RESOLUTION_1080P)
                            .build()//1080p
            );
    private final S3Service s3Service;

    public PlaylistGeneratorWorker(PlaylistGenerationStrategy playlistGenerator, S3Service s3Service) {
        this.playlistGenerator = playlistGenerator;
        this.s3Service = s3Service;
    }

    public String generatePlaylist(Media media)
    {
        /*todo
        1. Spawn 4 threads for generating playlist for each bitrate
        2. In each thread
            2.1 Fetch segments of that bitrate
            2.2 Generate Playlist for that bitrate
            2.3 Save that playlist in the cdn
            2.4 return the url of that playlist
        3. Update the master index file with the bitrate files
        4. store the master index file in cdn
        4. return the url of the master file
        */

        List<CompletableFuture<VariantPlaylistResult>> futures = bitrateConfigs.stream()
                .map(bc->
                        CompletableFuture.supplyAsync(()->
                                new VariantPlaylistResult(
                                        bc.getBandwidth(),
                                        bc.getResolution(),
                                        playlistGenerator.generate(
                                                media , bc
                                        )
                                )
                                )
                        ).toList();
       return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v->{
                    List<VariantPlaylistResult> urls =
                    futures.stream().
                            map(CompletableFuture::join)
                            .toList();
                 String masterPlaylist =generateMasterPlaylist(urls);
                 String masterKey =
                         "transcoded/"+media.getVideoAsset().getId()+"/playlists/master.m3u8";
                 String masterPlaylistURL = s3Service.uploadFileToS3(
                         Secrets.AWS_SECURE_BUCKET ,
                         masterKey ,
                         masterPlaylist.getBytes()
                 );
                 return masterPlaylistURL;
                }).join();
    }
    private String generateMasterPlaylist(List<VariantPlaylistResult> results) {

        StringBuilder sb = new StringBuilder(2048);

        sb.append("#EXTM3U\n");
        sb.append("#EXT-X-VERSION:3\n\n");

        for (VariantPlaylistResult result : results) {

            sb.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                    .append(result.bandwidth())
                    .append(",RESOLUTION=")
                    .append(result.resolution().getWidth())
                    .append("x")
                    .append(result.resolution().getHeight())
                    .append("\n");

            sb.append(result.url()).append("\n\n");
        }

        return sb.toString();
    }
    record VariantPlaylistResult(
            long bandwidth,
            ResolutionEnum resolution,
            String url
    ) {}
}
