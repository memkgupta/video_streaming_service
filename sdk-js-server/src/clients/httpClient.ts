import { SdkError } from "../errors/error.js";

export interface ClientOptions {
  baseUrl: string;
  accessKey: string;
  accessSecret:string;
  timeout?: number;
}


export class HttpClient  {
  private baseUrl: string;
  private headers: Record<string, string>;
  private timeout: number;
  constructor(options: ClientOptions) {
    this.baseUrl = options.baseUrl.replace(/\/$/, "");
    this.timeout = options.timeout ?? 10_000;
    this.headers = {
      "Content-Type": "application/json",
      "X-ACCESS-KEY": options.accessKey,
      "X-ACCESS-SECRET":options.accessSecret
    };
  }
  async request<T>(
    method: string,
    path: string,
    body?: unknown
  ): Promise<T> {
    const url = `${this.baseUrl}${path}`;
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), this.timeout);

    try {
      const res = await fetch(url, {
        method,
        headers: this.headers,
        body: body ? JSON.stringify(body) : undefined,
        signal: controller.signal,
      });

      const data = await res.json();

      if (!res.ok) {
        throw new SdkError(
          `Request failed: ${method} ${path}`,
          res.status,
          data
        );
      }

      return data as T;
    } finally {
      clearTimeout(timer);
    }
  }
  get = <T>(path: string) => this.request<T>("GET", path);
  post = <T>(path: string, body: unknown) => this.request<T>("POST", path, body);
  patch = <T>(path: string, body: unknown) => this.request<T>("PATCH", path, body);
  delete = <T>(path: string) => this.request<T>("DELETE", path);
  put = <T>(path:string,body:unknown)=>this.request<T>("PUT",path,body)
}