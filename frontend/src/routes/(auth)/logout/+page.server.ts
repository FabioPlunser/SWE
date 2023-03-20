import type { PageLoad } from "./$types";
import { BACKEND_URL } from "$env/static/private";
import { fail, redirect } from "@sveltejs/kit";

export const load = (async ({ params, cookies, fetch }) => {
  console.log("logout");

  let res = await fetch(`http://${BACKEND_URL}/api/logout`, { method: "POST" });
  res = await res.json();
  console.log(res);
  if (res.success) cookies.set("token", "");
  throw redirect(302, "/login");
}) satisfies PageLoad;
