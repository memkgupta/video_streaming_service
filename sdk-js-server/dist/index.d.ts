interface MediaCreateRequest {
    mediaType: MediaType;
    mediaAccessibility: MediaAccessibility;
    groupId: string;
    moderation: boolean;
}
declare enum MediaType {
    LIVE = 0,
    STATIC = 1
}
declare enum MediaAccessibility {
    PROTECTED = 0
}
declare enum MediaStatus {
    CREATED = 0,
    PROCESSING = 1,
    READY = 2,
    LIVE = 3,
    ENDED = 4,
    FAILED = 5,
    BLOCKED = 6
}
interface Media {
    id: string;
    active: boolean;
    mediaType: MediaType;
    userId: string;
    createdAt: Date;
    updatedAt: Date;
    accessibility: MediaAccessibility;
    status: MediaStatus;
    pushKey: string;
}
interface PageResponse<T> {
    data: T[];
    total: number;
    hasNext: boolean;
    hasPrevious: boolean;
    page: number;
}
interface FileMetaData {
    fileName: string;
    fileType: string;
    fileSize: number;
    fileUrl: string;
    uploadStatus: string;
    errorMessage: string;
    mediaId: string;
}
interface FileUploadStartResponse {
    key: string;
    uploadId: string;
    assetId?: string;
    pushKey?: string;
}
interface MediaAccessTokens {
    access_token: string;
    refresh_token: string;
}

declare class StreamOpsClient {
    private httpClient;
    constructor(access_key: string, access_secret: string, clientConfig?: {
        baseUrl: string;
    });
    createMedia(request: MediaCreateRequest): Promise<Media>;
    deleteMedia(request: string): Promise<void>;
    getAll(page?: number, limit?: number, params?: Map<String, String>): Promise<PageResponse<Media>>;
    updateThumbnail(mediaId: string, fileMetadata: FileMetaData): Promise<string>;
    startVideoUpload(mediaId: string, fileMetadata: FileMetaData): Promise<FileUploadStartResponse>;
    generateTokens(mediaId: string, userId: string): Promise<MediaAccessTokens>;
}

declare class SdkError extends Error {
    statusCode: number;
    body: unknown;
    constructor(message: string, statusCode: number, body: unknown);
}

export { type FileMetaData, type FileUploadStartResponse, type Media, type MediaAccessTokens, MediaAccessibility, type MediaCreateRequest, MediaStatus, MediaType, type PageResponse, SdkError, StreamOpsClient };
