<script lang="ts">
  import type { PageData } from "./$types";
  export let data: PageData;
</script>



<section class="flex justify-center items-center h-screen">
  <div class="w-full">
    <h1 class="flex justify-center text-5xl font-bold">PlantHealth</h1>
    <br/>
    <h1 class="flex justify-center text-4xl font-bold">Login</h1>

    <form method="POST" action="?/login">
      <div class="">
        <div class="mx-auto form-control w-full max-w-xs">
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="label">
            <span class="label-text font-bold">Username</span>
          </label>
          <input
            name = "username"
            type="text"
            placeholder="Type here"
            class="input input-bordered w-full bg-gray-800 text-white"
          />
        </div>
        <div class="mx-auto form-control w-full max-w-xs">
          <!-- svelte-ignore a11y-label-has-associated-control -->
          <label class="label">
            <span class="label-text font-bold">Password</span>
          </label>
          <input
            name = "password"
            type="text"
            placeholder="Type here"
            class="input input-bordered w-full bg-gray-800 text-white"
          />
        </div>
        <div class="flex justify-center mt-4">
          <button class="btn btn-primary">Login</button>
        </div>
      </div>
    </form>
  </div>
</section>
