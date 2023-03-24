import type { Actions, PageServerLoad } from "./$types";
import { BACKEND_URL } from "$env/static/private";
import { fail, redirect, error } from "@sveltejs/kit";
import { z } from "zod";

export const load = (async ({ fetch }) => {
  // let res = ;
  // res = await res.json();
  let res = await fetch(`http://${BACKEND_URL}/api/get-all-users`);
  res = await res.json();
  if (!res.success) {
    throw error(404, {
      message: "Not found",
    });
  }

  if (res.success) {
    return { users: res };
  }
  return { users: [] };
}) satisfies PageServerLoad;

const schema = z.object({
  username: z
    .string({ required_error: "Username is required" })
    .min(1, { message: "Username is required" })
    .max(64, { message: "Username must be less than 64 characters" })
    .trim(),

  email: z
    .string({ required_error: "Email is required" })
    .email({ message: "Email must be a valid email address" })
    .min(1, { message: "Email is required" })
    .max(64, { message: "Email must be less than 64 characters" })
    .trim(),

  password: z
    .string({ required_error: "Password is required" })
    .min(1, { message: "Password is required" })
    .min(6, { message: "Password must be at least 6 characters" })
    .max(32, { message: "Password must be less than 32 characters" })
    .trim(),

  passwordConfirm: z
    .string({ required_error: "Password is required" })
    .min(1, { message: "Password is required" })
    .min(6, { message: "Password must be at least 6 characters" })
    .max(32, { message: "Password must be less than 32 characters" })
    .trim(),
});

export const actions = {
  createUser: async ({ cookies, request, fetch, event }) => {
    const formData = await request.formData();
    const zod = schema.safeParse(Object.fromEntries(formData));
    console.log(formData);

    if (formData.get("password") !== formData.get("passwordConfirm")) {
      return fail(400, { error: true, errors: "Passwords do not match" });
    }

    if (!zod.success) {
      // Loop through the errors array and create a custom errors array
      const errors = zod.error.errors.map((error) => {
        return {
          field: error.path[0],
          message: error.message,
        };
      });

      return fail(400, { error: true, errors });
    }

    var requestOptions = {
      method: "POST",
      body: formData,
    };

    let res = await fetch(
      `http://${BACKEND_URL}/api/create-user`,
      requestOptions
    ).catch((error) => console.log("error", error));

    res = await res.json();
    console.log(res);

    if (res.success) {
      cookies.set(
        "token",
        JSON.stringify({
          token: res.token,
          permissions: res.permissions,
          personId: res.personId,
        })
      );
      // throw redirect(302, "/");
    } else {
      // TODO: add to toast notifications.
      return fail(400, { error: true, errors: res.message });
    }
  },

  updateUser: async ({ fetch, event }) => {
    const formData = new FormData();
  },

  deleteUser: async ({ fetch, event }) => {
    const formData = new FormData();
    formData.append("id", event.query.get("id"));

    var requestOptions = {
      method: "POST",
      body: formData,
    };

    let res = await fetch(
      `http://${BACKEND_URL}/api/delete-user`,
      requestOptions
    ).catch((error) => console.log("error", error));

    res = await res.json();

    if (res.success) {
      // throw redirect(302, "/");
    }
  },
} satisfies Actions;
