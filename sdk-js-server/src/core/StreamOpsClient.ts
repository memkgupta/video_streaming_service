import { ClientOptions, HttpClient } from "../clients/httpClient";
import { FileMetaData, FileUploadStartResponse, Media, MediaAccessTokens, MediaCreateRequest, PageResponse } from "../types/types";

export class StreamOpsClient
{

    
    private httpClient:HttpClient
    constructor(access_key:string,access_secret:string,clientConfig?:{baseUrl:string})
    {
     
        this.httpClient = new HttpClient({
            accessKey:access_key,
            accessSecret:access_secret,
            baseUrl:clientConfig?.baseUrl||"http://localhost:8001/api/asset_onboarding/v1/media",
            timeout:5000
        })
    }

    async createMedia(request:MediaCreateRequest):Promise<Media>{
            const req =await this.httpClient.post<Media>("",request);
            return req;
    }

    async deleteMedia(request:string):Promise<void>{
        await this.httpClient.delete(`/${request}`)
    }
    async getAll(page:number=1 , limit:number=10,params?:Map<String,String>):Promise<PageResponse<Media>>{
        const res = await this.httpClient.get<PageResponse<Media>>("");
        return res;
    }
    async updateThumbnail(mediaId:string,fileMetadata:FileMetaData):Promise<string>{
        const res = await this.httpClient.put<{preSignedURL:string}>(`/${mediaId}/thumbnail`,fileMetadata);
        return res.preSignedURL;
    }
    async startVideoUpload(mediaId:string,fileMetadata:FileMetaData):Promise<FileUploadStartResponse>{
        const res = await this.httpClient.post<FileUploadStartResponse>(`/${mediaId}/video`,fileMetadata);
        return res;
    }
    async generateTokens(mediaId:string , userId:string):Promise<MediaAccessTokens>{
        const res = await this.httpClient.get<MediaAccessTokens>(`/${mediaId}/generate-tokens?userId=${userId}`);
        return res;
    }

}