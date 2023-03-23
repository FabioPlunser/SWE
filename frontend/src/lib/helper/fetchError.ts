export function checkError(response: Response) {
  if (response.ok) {
    return response;
  } else {
    throw new Error(response.statusText);
  }
}
