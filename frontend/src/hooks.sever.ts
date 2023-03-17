import type { Handle } from '@sveltejs/kit';
import { authenticate } from "$helper/auth";
import { redirect, type Handle } from "@sveltejs/kit"

export const handle = (async ({ event, resolve }) => {

  event.locals.user = authenticate(event);
  console.log(event.locals.user);
  if(!event.locals.user){
    return redirect(302, "/login");
  }
  // if (event.url.pathname.startsWith('/user')) {
  //   return new Response('custom response');
  // }
  // if(event.url.pathname.startsWith('/gardener')){
  //   return new Response('custom response');

  // }
  // if(event.url.pathname.startsWith('/admin')){
  //   return new Response('custom response');

  // }
 
  // const response = await resolve(event);
  // return response;
}) satisfies Handle;