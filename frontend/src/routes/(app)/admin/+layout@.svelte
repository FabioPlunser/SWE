<script lang="ts">
  import { page } from "$app/stores";
  import { fly } from "svelte/transition";

  import PageTransition from "$components/PageTransition.svelte";
  import Mobile from "$helper/Mobile.svelte";
  import Query from "$helper/Query.svelte";

  import AdminNav from "$components/ui/adminNav.svelte";
  import Nav from "$components/ui/Nav.svelte";
  import type { LayoutData } from "./$types";
  export let data: LayoutData = null;

  let links = [
    {
      name: "Home",
      path: "/admin",
    },
    {
      name: "Plants",
      path: "/admin/plants",
    },
    {
      name: "Gardener",
      path: "/admin/gardener",
    },
    {
      name: "Users",
      path: "/admin/users",
    },
    {
      name: "Settings",
      path: "/admin/settings",
    },
  ];

  // $: console.log($page.url);
</script>

<!--

<PageTransition url={$page.url.pathname}>
  <Nav user={data.role}/>

  <main class="mx-4 min-h-screen">
    <slot />
  </main>

  <div class="fixed bottom-0 mb-4 z-50 w-full">
    <AdminNav />
  </div>
</PageTransition>
-->

<PageTransition url={$page.url.pathname}>
  <Nav user={data.role} />
  <main class="mx-4 h-screen">
    <slot />
  </main>
  <div class="fixed bottom-0 mb-4 z-50 w-full">
    <AdminNav />
  </div>
</PageTransition>
