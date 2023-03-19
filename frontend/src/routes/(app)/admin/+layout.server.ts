
import type { LayoutServerLoad } from './$types';
 
export const load = (async ({locals}) => {
  const {user} = locals;
  if (!user) {
    return {status: 401};
  }
  return user;
}) satisfies LayoutServerLoad;