import type { PageServerLoad } from "./$types";
import { redirect } from "@sveltejs/kit";

export const load = (async ({ locals }) => {
  if (!locals.user) {
    throw redirect(302, "/login");
    return { success: false };
  } else {
    if (
      !locals.user.permissions.includes("ADMIN") &&
      !locals.user.permissions.includes("GARDENER") &&
      !locals.user.permissions.includes("USER")
    ) {
      throw redirect(302, "/login");
      return { success: false };
    }
    // TODO: check if token is valid
    // redirect to according pag
    if (locals.user.permissions.includes("ADMIN"))
      throw redirect(307, "/admin");
    if (locals.user.permissions.includes("GARDENER"))
      throw redirect(307, "/gardener");
    if (locals.user.permissions.includes("USER")) throw redirect(307, "/user");
  }

  return { success: true };
}) satisfies PagServerLoad;
