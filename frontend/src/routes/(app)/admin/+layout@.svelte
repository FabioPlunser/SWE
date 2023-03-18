<script lang="ts">
  import { page } from "$app/stores";
  import AdminNav from "$components/ui/adminNav.svelte";
  import Nav from "$components/ui/Nav.svelte";
  import PageTransition from "$components/PageTransition.svelte";
  import type { LayoutData } from './$types';
  export let data:LayoutData = null; 
</script>


<Nav user={data.role}/>
<PageTransition url={$page.url.pathname}>
  <main class="mx-4 min-h-screen">
    <slot />
  </main>
</PageTransition>

{#if data.role === 'ADMIN'}
<div class="fixed bottom-0 mb-4 z-50 w-full">
  <AdminNav />
</div>
{/if}
