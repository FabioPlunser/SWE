
import type { RequestEvent } from "@sveltejs/kit"


export function authenticate(event: RequestEvent){
  const { cookies } = event;
  const token = cookies.get("token");
  console.log(token);
  if(!token){
    return null;
  }

  return "test";
}