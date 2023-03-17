import type { PageServerLoad } from "./$types";
import { redirect } from "@sveltejs/kit";

export const load = (async ({ cookies }) => {
  const token = cookies.get("token");

  if (!token) {
    throw redirect(302, "/login");
    return { success: false };
  } else {
    // TODO check if token is valid
  }

  return { success: true }
  
  
}) satisfies PagServerLoad;
