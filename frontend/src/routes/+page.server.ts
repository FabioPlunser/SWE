import type { PageServerLoad } from "./$types";
import { redirect } from "@sveltejs/kit";
import { BACKEND_URL } from "$env/static/private";

export const load = (async ({ locals, fetch }) => {
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
    // get user permissions from backend
    let res = await fetch(`http://${BACKEND_URL}/api/get-user-permissions`);
    res = await res.json();
    if (res.success) {
      if (locals.user.permissions.toString() !== res.permissions.toString()) {
        throw redirect(302, "/logout");
      }
    }
    // redirect to according pag
    if (locals.user.permissions.includes("ADMIN"))
      throw redirect(307, "/admin");
    if (locals.user.permissions.includes("GARDENER"))
      throw redirect(307, "/gardener");
    if (locals.user.permissions.includes("USER")) throw redirect(307, "/user");
  }

  return { success: true };
}) satisfies PagServerLoad;
