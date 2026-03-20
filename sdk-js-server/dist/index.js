"use strict";
var __defProp = Object.defineProperty;
var __getOwnPropDesc = Object.getOwnPropertyDescriptor;
var __getOwnPropNames = Object.getOwnPropertyNames;
var __hasOwnProp = Object.prototype.hasOwnProperty;
var __export = (target, all) => {
  for (var name in all)
    __defProp(target, name, { get: all[name], enumerable: true });
};
var __copyProps = (to, from, except, desc) => {
  if (from && typeof from === "object" || typeof from === "function") {
    for (let key of __getOwnPropNames(from))
      if (!__hasOwnProp.call(to, key) && key !== except)
        __defProp(to, key, { get: () => from[key], enumerable: !(desc = __getOwnPropDesc(from, key)) || desc.enumerable });
  }
  return to;
};
var __toCommonJS = (mod) => __copyProps(__defProp({}, "__esModule", { value: true }), mod);

// src/index.ts
var index_exports = {};
__export(index_exports, {
  MediaAccessibility: () => MediaAccessibility,
  MediaStatus: () => MediaStatus,
  MediaType: () => MediaType,
  SdkError: () => SdkError,
  StreamOpsClient: () => StreamOpsClient
});
module.exports = __toCommonJS(index_exports);

// src/errors/error.ts
var SdkError = class extends Error {
  constructor(message, statusCode, body) {
    super(message);
    this.statusCode = statusCode;
    this.body = body;
    this.name = "SdkError";
  }
};

// src/clients/httpClient.ts
var HttpClient = class {
  constructor(options) {
    this.get = (path) => this.request("GET", path);
    this.post = (path, body) => this.request("POST", path, body);
    this.patch = (path, body) => this.request("PATCH", path, body);
    this.delete = (path) => this.request("DELETE", path);
    this.put = (path, body) => this.request("PUT", path, body);
    this.baseUrl = options.baseUrl.replace(/\/$/, "");
    this.timeout = options.timeout ?? 1e4;
    this.headers = {
      "Content-Type": "application/json",
      "X-ACCESS-KEY": options.accessKey,
      "X-ACCESS-SECRET": options.accessSecret
    };
  }
  async request(method, path, body) {
    const url = `${this.baseUrl}${path}`;
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), this.timeout);
    console.log(this.headers);
    try {
      const res = await fetch(url, {
        method,
        headers: this.headers,
        body: body ? JSON.stringify(body) : void 0,
        signal: controller.signal
      });
      const data = await res.json();
      if (!res.ok) {
        throw new SdkError(
          `Request failed: ${method} ${path}`,
          res.status,
          data
        );
      }
      return data;
    } finally {
      clearTimeout(timer);
    }
  }
};

// src/core/StreamOpsClient.ts
var StreamOpsClient = class {
  constructor(access_key, access_secret, clientConfig) {
    this.httpClient = new HttpClient({
      accessKey: access_key,
      accessSecret: access_secret,
      baseUrl: clientConfig?.baseUrl || "http://localhost:8001/api/asset_onboarding/v1/media",
      timeout: 5e3
    });
  }
  async createMedia(request) {
    const req = await this.httpClient.post("", request);
    return req;
  }
  async deleteMedia(request) {
    await this.httpClient.delete(`/${request}`);
  }
  async getAll(page = 1, limit = 10, params) {
    const res = await this.httpClient.get("");
    return res;
  }
  async updateThumbnail(mediaId, fileMetadata) {
    const res = await this.httpClient.put(`/${mediaId}/thumbnail`, fileMetadata);
    return res.preSignedURL;
  }
  async startVideoUpload(mediaId, fileMetadata) {
    const res = await this.httpClient.post(`/${mediaId}/video`, fileMetadata);
    return res;
  }
  async generateTokens(mediaId, userId) {
    const res = await this.httpClient.get(`/${mediaId}/generate-tokens?userId=${userId}`);
    return res;
  }
};

// src/types/types.ts
var MediaType = /* @__PURE__ */ ((MediaType2) => {
  MediaType2[MediaType2["LIVE"] = 0] = "LIVE";
  MediaType2[MediaType2["STATIC"] = 1] = "STATIC";
  return MediaType2;
})(MediaType || {});
var MediaAccessibility = /* @__PURE__ */ ((MediaAccessibility2) => {
  MediaAccessibility2[MediaAccessibility2["PROTECTED"] = 0] = "PROTECTED";
  return MediaAccessibility2;
})(MediaAccessibility || {});
var MediaStatus = /* @__PURE__ */ ((MediaStatus2) => {
  MediaStatus2[MediaStatus2["CREATED"] = 0] = "CREATED";
  MediaStatus2[MediaStatus2["PROCESSING"] = 1] = "PROCESSING";
  MediaStatus2[MediaStatus2["READY"] = 2] = "READY";
  MediaStatus2[MediaStatus2["LIVE"] = 3] = "LIVE";
  MediaStatus2[MediaStatus2["ENDED"] = 4] = "ENDED";
  MediaStatus2[MediaStatus2["FAILED"] = 5] = "FAILED";
  MediaStatus2[MediaStatus2["BLOCKED"] = 6] = "BLOCKED";
  return MediaStatus2;
})(MediaStatus || {});
// Annotate the CommonJS export names for ESM import in node:
0 && (module.exports = {
  MediaAccessibility,
  MediaStatus,
  MediaType,
  SdkError,
  StreamOpsClient
});
//# sourceMappingURL=index.js.map