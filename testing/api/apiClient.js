const axios = require("axios");
const apiClient = (service,version)=>{
   
    return axios.create({
 baseURL: `${process.env.BASE_URL}/${service}${version ?? ""}`,
  headers: {
    "Content-Type": "application/json",
  },
});
}

module.exports = apiClient