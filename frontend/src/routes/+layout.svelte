<script lang="ts">
  import "../app.css";
  import PageTransition from '$components/PageTransition.svelte';
  import { page } from '$app/stores';
</script>

<PageTransition url={$page.url.pathname}>
  <main class="m-10 min-h-screen">
      <slot />
  </main>
</PageTransition>