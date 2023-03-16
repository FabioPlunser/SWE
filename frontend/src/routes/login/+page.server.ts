import type { Actions } from './$types';
 
export const actions = {
  login: async ({cookies, request, fetch}) => {
    const data = await request.formData();
    const username = data.get('username');
    const password = data.get('password');

    console.log(username);


    const res = await fetch('http://localhost:3000/api/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        username,
        password
      })
    });

    const json = await res.json();

    if (json.success) {
      cookies.set('token', json.token);
      return {
        status: 302,
        headers: {
          location: '/'
        }
      };
    }
  }
} satisfies Actions;