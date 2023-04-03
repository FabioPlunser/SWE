<script lang="ts">
  export let pictureRefs: string[];
</script>

<ul
  class="flex overflow-x-auto gap-6 snap-x snap-mandatory before:shrink-0 before:w-[30vw] after:shrink-0 after:w-[30vw]"
>
  {#each pictureRefs as ref}
    <li class="shrink-0 snap-center">
      <img alt="Plant" src={ref} />
    </li>
  {/each}
</ul>
