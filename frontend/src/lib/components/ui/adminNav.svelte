<script lang="ts">
  import Icon from "$components/ui/Icon.svelte";

  let icons = ["home", "plant", "garden", "settings"]
  let size = "40";
</script>


<div class="mx-2">
  <div class="w-full p-4 rounded-2xl bg-base-100 drop-shadow-2xl">
    <div class="flex items-center gap-4">
      <div>
        <Icon name="home" w={size} h={size} class="dark:invert mx-auto" />
        <h1 class="flex justify-center ">Home</h1>
      </div>
      <div>
        <Icon name="potted-plant" w={size} h={size} class="dark:invert mx-auto" />
        <h1 class="flex justify-center ">Plants</h1>
      </div>
      <div>
        <Icon name="gardening-shears" w={size} h={size} class="dark:invert mx-auto" />
        <h1 class="flex justify-center mx-auto ">Gardeners</h1>
      </div>
      <div>
        <Icon name="group" w={size} h={size} class="dark:invert mx-auto" />
        <h1 class="flex justify-center mx-auto ">Users</h1>
      </div>
      <div>
        <Icon name="gear" w={size} h={size} class="dark:invert flex items-center" />
        <h1 class="flex justify-center mx-auto ">Users</h1>
      </div>
    </div>
  </div>
</div>