<script lang="ts">
  import { page } from "$app/stores";
  import Nav from "$components/ui/Nav.svelte";
  import PageTransition from "$components/PageTransition.svelte";
</script>

<PageTransition url={$page.url.pathname}>
  <Nav />
  <main class="mx-10 min-h-screen">
    <slot />
  </main>
</PageTransition>
