<script lang="ts">
  import { enhance } from "$app/forms";
  import type { ActionData, PageData } from "./$types";
  import FormError from "$helper/formError.svelte";
  import Logo from "$assets/Logo.svg?url";
  // export let data: PageData;
  export let form: ActionData;
</script>

<section class="flex justify-center items-center h-screen">
  <div class="w-full">
    <img
      src={Logo}
      class="flex justify-center mx-auto w-auto h-auto"
      alt="Logo"
    />
    <h1 class="flex justify-center text-5xl font-bold">PlantHealth</h1>
    <br />
    <h1 class="flex justify-center text-4xl font-bold">Login</h1>

    <form method="POST" action="?/login" use:enhance>
      <div class="">
        <div class="mx-auto form-control w-full max-w-xs">
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="label">
            <span class="label-text font-bold">Username</span>
          </label>
          <input
            name="username"
            type="text"
            placeholder="Type here"
            class="input input-bordered w-full bg-gray-800 text-white dark:bg-base-300"
          />
          <FormError field="username" {form} />
        </div>
        <div class="mx-auto form-control w-full max-w-xs">
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="label">
            <span class="label-text font-bold">Password</span>
          </label>
          <input
            name="password"
            type="password"
            placeholder="Type here"
            class="input input-bordered w-full bg-gray-800 text-white dark:bg-base-300"
          />
          <FormError field="password" {form} />
        </div>
        <div class="flex justify-center mt-4">
          <button class="btn btn-primary">Login</button>
        </div>
      </div>
    </form>
  </div>
</section>
