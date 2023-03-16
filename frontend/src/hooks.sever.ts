import type { Handle } from '@sveltejs/kit';
import { authenticate } from "$helper/auth";
import { redirect, type Handle } from "@sveltejs/kit"

export const handle = (async ({ event, resolve }) => {
  if (event.url.pathname.startsWith('/user')) {
    return new Response('custom response');
  }
  if(event.url.pathname.startsWith('/gardener')){
    return new Response('custom response');

  }
  if(event.url.pathname.startsWith('/admin')){
    return new Response('custom response');

  }
 
  const response = await resolve(event);
  return response;
}) satisfies Handle;