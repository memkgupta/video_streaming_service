package com.vsnt.transcoder.docker_utils;

import com.vsnt.transcoder.CapacityReachedException;
import com.vsnt.transcoder.config.Secrets;
import com.vsnt.transcoder.dtos.TranscodingJob;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*Responsible for picking a free container and assigning the transcoding job to it*/
@Component
public class JobAssigner {
private final WorkerSemaphore workerSemaphore;
private final ContainerSpawner containerSpawner;
    public JobAssigner(WorkerSemaphore workerSemaphore , ContainerSpawner containerSpawner) {
        this.workerSemaphore = workerSemaphore;
        this.containerSpawner = containerSpawner;
    }
    public void assignJob(TranscodingJob job) throws CapacityReachedException
    {

        if(workerSemaphore.tryAcquire())
        {
            try{
                Map<String,Object> args = new HashMap<>();
                args.put("mediaId" , job.getJobId());
                args.put("encryptionKey",job.getEncryptionKey());
                args.put("assetKey",job.getKey());
                args.put("assetId",job.getAssetId());
                this.containerSpawner.spawn(Secrets.DOCKER_TRANSCODER_CONTAINER_IMAGE,args
                );

            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            finally {
                workerSemaphore.release();
            }
        }
        else {
            throw new CapacityReachedException("Containers capacity reached");
        }

    }
}
