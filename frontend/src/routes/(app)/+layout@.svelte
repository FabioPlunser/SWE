<script lang="ts">
  import { page } from "$app/stores";
  import Nav from "$components/ui/Nav.svelte";
  import PageTransition from "$components/PageTransition.svelte";
  import { Toaster } from "svelte-french-toast";
  import type { LayoutData } from "./$types";
  export let data: LayoutData = null;
</script>

<Toaster />
<Nav user={data.permissions} />
<PageTransition url={$page.url.pathname}>
  <main class="mx-4 h-screen">
    <slot />
  </main>
</PageTransition>
