import type { PageServerLoad } from "./$types";
import { redirect } from "@sveltejs/kit";

export const load = (async ({ cookies }) => {
  let token = cookies.get("token");

  if (!token) {
    throw redirect(302, "/login");
    return { success: false };
  } else {
    token = JSON.parse(token);
    if (
      !token.permissions.includes("ADMIN") &&
      !token.permissions.includes("GARDENER") &&
      !token.permissions.includes("USER")
    ) {
      throw redirect(302, "/login");
      return { success: false };
    }
    // TODO: check if token is valid
    // redirect to according pag
    if (token.permissions.includes("ADMIN")) throw redirect(307, "/admin");
    if (token.permissions.includes("GARDENER"))
      throw redirect(307, "/gardener");
    if (token.permissions.includes("USER")) throw redirect(307, "/user");
  }

  return { success: true };
}) satisfies PagServerLoad;
