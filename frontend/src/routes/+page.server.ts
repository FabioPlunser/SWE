import type { PageServerLoad } from "./$types";
import { redirect } from "@sveltejs/kit";

export const load = (async ({ cookies }) => {
  let token = cookies.get("token");
  console.log("token", token);
  if (!token) {
    throw redirect(302, "/login");
    return { success: false };
  } else {
    token = JSON.parse(token);
    console.log("token-role", token.role);
    if(token.role !== "ADMIN" && token.role !== "GARDENER" && token.role !== "USER"){
      console.log("redirect");
      throw redirect(302, "/login");
      return { success: false };
    }
    console.log("redirecting to " + token.role.toLowerCase());
    // TODO check if token is valid
    // redirect to according page 
    throw redirect(307, "/" + token.role.toLowerCase());
  }
  
  return { success: true }
  
  
}) satisfies PagServerLoad;
