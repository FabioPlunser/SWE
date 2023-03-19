import type { Handle } from '@sveltejs/kit';
import { redirect, error} from '@sveltejs/kit';




export const handle = (async ({ event, resolve }) => {

  const { cookies } = event;
  let token = cookies.get("token");
  if(token){
    token = JSON.parse(token);
  }else{
    const response = await resolve(event);
    return response;
  }


  event.locals.user = token;

  if (event.url.pathname.startsWith("/login")) {
    if (event.locals.user) {
      throw redirect(307, "/");
    }
  }
  
  if (event.url.pathname.startsWith("/admin")) {
    if (event.locals.user.role !== "ADMIN") {
      throw redirect(307, "/");
    }
  }

  if(event.url.pathname.startsWith("/gardener")){
    if(event.locals.user.role !== "GARDENER"){
      throw redirect(307, "/");
    }
  }

  if(event.url.pathname.startsWith("/user")){
    if(event.locals.user.role !== "USER"){
      throw redirect(307, "/");
    }
  }
 
  const response = await resolve(event);
  return response;
}) satisfies Handle;