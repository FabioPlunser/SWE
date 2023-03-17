<script lang="ts">
  import { page } from "$app/stores";

  import Home from "$assets/icons/home.svg?component";
  import Plant from "$assets/icons/potted-plant.svg?component";
  import Gardener from "$assets/icons/gardening-shears.svg?component";
  import Group from "$assets/icons/group.svg?component";
  import Settings from "$assets/icons/gear.svg?component";

  $: path = $page.url.pathname;
  console.log(path);
  let size = "45";
</script>


<div class="mx-2">
  <div class=" p-4 rounded-2xl bg-base-100 dark:backdrop-blur-4xl drop-shadow-3xl">
    <div class="flex items-center gap-4 justify-center">
      <!-- {#key path} -->
        <a href="/admin">
          <div>
            <Home name="home" width={size} height={size} class="dark:fill-white mx-auto drop-shadow-3xl {path === "/admin" ? "rounded-full bg-primary p-1" : "" }" />
            <!-- <h1 class="flex justify-center ">Home</h1> -->
          </div>
        </a>
        <a href="/admin/plants">
          <div>
            <Plant name="potted-plant" width={size} height={size} class="dark:fill-white mx-auto {path.includes("/admin/plants") ? "rounded-full bg-primary p-1" : "" }" />
            <!-- <h1 class="flex justify-center ">Plants</h1> -->
          </div>
        </a>
        <a href="/admin/gardener">
          <div>
            <Gardener name="gardening-shears" width={size} height={size} class="dark:fill-white mx-auto {path.includes("/admin/gardener") ? "rounded-full bg-primary p-1" : "" }" />
            <!-- <h1 class="flex justify-center mx-auto ">Gardener</h1> -->
          </div>
        </a>
        <a href="/admin/users">
          <div>
            <Group name="group" width={size} height={size} class="dark:fill-white mx-auto {path.includes("/admin/users") ? "rounded-full bg-primary p-1" : "" }" />
            <!-- <h1 class="flex justify-center mx-auto ">Users</h1> -->
          </div>
        </a>
        <a href="/admin/settings">
          <div>
            <Settings name="gear" width={size} height={size} class="dark:fill-white mx-auto {path.includes("/admin/settings") ? "rounded-full bg-primary p-1" : "" }" />
            <!-- <h1 class="flex justify-center mx-auto ">Settings</h1> -->
          </div>
        </a>
      <!-- {/key} -->
    </div>
  </div>
</div>