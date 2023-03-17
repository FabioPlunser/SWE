<script lang="ts">
  import { page } from "$app/stores";
  import { enhance } from "$app/forms";
  import type { ActionData, PageData } from "./$types";

  export let data: PageData;
  $: console.log(data);
  export let form: ActionData;
  $: console.log(form);
</script>

<section class="flex justify-center items-center h-screen">
  <div class="w-full">
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
          {#if form?.error}
            {#each form?.errors as error}
              {#if error.field === 'username'}
                <p class="text-red-500 text-xs italic">{error.message}</p>
              {/if}
            {/each}
          {/if}
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
          {#if form?.error}
          {#each form?.errors as error}
            {#if error.field === 'password'}
              <p class="text-red-500 text-xs italic">{error.message}</p>
            {/if}
          {/each}
        {/if}
        </div>
        <div class="flex justify-center mt-4">
          <button class="btn btn-primary">Login</button>
        </div>
      </div>
    </form>
  </div>
</section>
