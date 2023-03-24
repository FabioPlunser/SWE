<script lang="ts">
  import { page } from "$app/stores";
  import Logo from "$assets/Logo.svg?url";
</script>

<section class="flex justify-center items-center h-screen">
  <div>
    <img
      src={Logo}
      class="flex justify-center mx-auto w-auto h-auto"
      alt="Logo"
    />
    <h1 class="flex justify-center text-5xl font-bold">PlantHealth</h1>
    <br />
    <p class="text-grey font-bold text-3xl flex justify-center">
      {$page.status}
    </p>
    <br />
    <p class="text-black text-4xl flex justify-center">
      Oops! Page {$page.error?.message}
    </p>
  </div>
</section>
