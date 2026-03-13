export interface MediaCreateRequest {
  mediaType: MediaType
  mediaAccessibility: MediaAccessibility
  groupId: string
  moderation: boolean
}
export enum MediaType{
    LIVE,
    STATIC
}
export enum MediaAccessibility{
    PROTECTED
}
export enum MediaStatus{
        CREATED ,
    PROCESSING,
    READY,
    LIVE,
    ENDED,
    FAILED,
    BLOCKED
}
export interface Media{
      id: string
  active: boolean
  mediaType: MediaType
  userId: string
  createdAt: Date
  updatedAt: Date
  accessibility: MediaAccessibility
 
  status: MediaStatus
  pushKey: string
}
export interface PageResponse<T> {
  data: T[]
  total: number
  hasNext: boolean
  hasPrevious: boolean
  page: number
}
export interface FileMetaData {
  fileName: string
  fileType: string
  fileSize: number
  fileUrl: string
  uploadStatus: string
  errorMessage: string
  mediaId: string
}
export interface FileUploadStartResponse {
  key: string
  uploadId: string
  assetId?: string
  pushKey?: string
}
export interface MediaAccessTokens{
    access_token:string,
    refresh_token:string
}