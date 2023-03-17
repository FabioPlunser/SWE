<script lang="ts">
  import { page } from "$app/stores";
  import Nav from "$components/ui/Nav.svelte";
  import PageTransition from "$components/PageTransition.svelte";
</script>

<Nav />

<PageTransition url={$page.url.pathname}>
  <main class="mx-4 min-h-screen">
    <slot />
  </main>
</PageTransition>
