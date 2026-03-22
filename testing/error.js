 function handleError(fnName, error) {

  if (error.response) {
    console.error(`[${fnName}] Server error ${error.response.status}:`, error.response.data);
  } else if (error.request) {
    console.error(`[${fnName}] No response received:`, error.message);
  } else {
    console.error(`[${fnName}] Request setup error:`, error.message);
  }
  throw error;
}

module.exports = handleError