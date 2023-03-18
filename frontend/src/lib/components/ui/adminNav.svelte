<script lang="ts">
  import { page } from "$app/stores";
  import { fly, slide } from "svelte/transition";
  import { horizontalSlide } from "$helper/transitions";

  import Home from "$assets/icons/home.svg?component";
  import Plant from "$assets/icons/potted-plant.svg?component";
  import Gardener from "$assets/icons/gardening-shears.svg?component";
  import Group from "$assets/icons/group.svg?component";
  import Settings from "$assets/icons/gear.svg?component";
  import { onMount } from "svelte";


  let rendered = false; 
  $: path = $page.url.pathname;
  onMount(() => {
    rendered = true;
  });
  let size = "45";

  let icons = [
    {
      name: "home",
      path: "/admin",
      icon: Home,
    },
    {
      name: "plant",
      path: "/admin/plants",
      icon: Plant,
    },
    {
      name: "gardener",
      path: "/admin/gardener",
      icon: Gardener,
    },
    {
      name: "group",
      path: "/admin/users",
      icon: Group,
    },
    {
      name: "settings",
      path: "/admin/settings",
      icon: Settings,
    },
  ]
</script>

{#if rendered}
  <div class="mx-2" in:fly={{y: 200, duration: 400}} out:fly={{y: -200, duration: 500}}>
    <div class=" p-4 rounded-2xl bg-base-100 backdrop-blur-4xl dark:bg-white/10 drop-shadow-3xl">
      <div class="flex items-center gap-4 justify-center" transition:horizontalSlide={{delay: 300, duration: 400}}>
        {#each icons as icon}
          <div>
            <a href={icon.path}>
              <div>
                <svelte:component this={icon.icon} width={size} height={size} class="dark:fill-white mx-auto drop-shadow-3xl {path === icon.path ? "rounded-full bg-primary p-1" : "" }"/>
              </div>
            </a>
          </div>
        {/each}
       
      </div>
    </div>
  </div>
{/if}