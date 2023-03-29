import { redirect } from "@sveltejs/kit";
import { BACKEND_URL } from "$env/static/private";

export async function load (locals, cookies){
  let res = await fetch(`http://${BACKEND_URL}/api/logout`);
  res = await res.json();

  cookies.set("token", "");

  throw redirect(302, "/login");

  return { success: true };
}