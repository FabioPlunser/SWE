import type { Actions } from "./$types";
import { redirect, error, fail } from "@sveltejs/kit";
import { z } from 'zod';
 
const loginSchema = z.object({
  username: z
			.string({ required_error: 'Name is required' })
			.min(1, { message: 'Name is required' })
			.max(64, { message: 'Name must be less than 64 characters' })
			.trim(),
  password: z
			.string({ required_error: 'Password is required' })
			.min(6, { message: 'Password must be at least 6 characters' })
			.max(32, { message: 'Password must be less than 32 characters' })
			.trim(),
});


export const actions = {
  login: async ({ cookies, request, fetch }) => {
    const formdata = Object.fromEntries(await request.formData());
    const loginData = loginSchema.safeParse(formdata);
    console.log(loginData);

    if(!loginData.success) {
      const errors = loginData.error.errors.map((error) => {
        return {
          field: error.path[0],
          message: error.message
        };
      });
      console.log(errors);
      return fail(400, { error: true,  errors });
    }
    
    
    // const username = data.get("username");
    // const password = data.get("password");
    
    // try {
    //   const res = await fetch(
    //     `http://localhost:8443/api/login/?username=${username}&password=${password}`,
    //     {
    //       method: "POST",
    //       headers: {
    //         "Content-Type": "application/json",
    //       },
    //     }
    //   );
    //   const json = await res.json();
    //   console.log(json);

    //   if (json.success) {
    //     cookies.set("token", json.token);
    //     throw redirect(302, "/");
    //   } else {
    //     return fail(400, { error: "Login failed" });
    //   }
    // } catch (error) {
    //   console.log(error);
    //   return fail(400, { error: "Login failed" });
    // }
    // return { success: true };
  },
} satisfies Actions;
