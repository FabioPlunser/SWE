<script lang="ts">
  export let pictures: string; 
</script>

<div class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-4">
  {#each {length: 10} as _, i}
    <div class="shrink-0 snap-center">
      <img alt="Plant" class="rounded-xl shadow-xl backdrop-blur-2xl" src="{pictures}" />
      <h1 class="flex justify-center">Picture: {i+1}</h1>
    </div>
  {/each}
</div>
