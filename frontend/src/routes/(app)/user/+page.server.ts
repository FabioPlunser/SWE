
import { BACKEND_URL } from "$env/static/private";

export async function load(locals){
  let res = await fetch(`http://${BACKEND_URL}/api/get-user-permissions`);
  res = await res.json();
  console.log("user", res);
};
