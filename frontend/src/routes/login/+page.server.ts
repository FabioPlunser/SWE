import type { Actions } from './$types';
import { redirect, error, invalid } from '@sveltejs/kit';


export const actions = {
  login: async ({cookies, request, fetch}) => {
    const data = await request.formData();
    const username = data.get('username');
    const password = data.get('password');

    console.log(username);


    // TODO: post request to backendd

    const res = await fetch(`http://localhost:8443/api/login/?username=${username}&password=${password}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
        },
    });

    const json = await res.json();
    console.log(json);


    if (json.success) {
      cookies.set('token', json.token);
      throw redirect(302, '/');
    }else{
      // return invalid(400, { error: 'Incorrect username or password' });
      return {success: false}
    }
  }
} satisfies Actions;