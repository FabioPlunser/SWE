<script lang="ts">
  export let pictureRefs: string[];
</script>

<ul>
  {#each pictureRefs as ref}
    <li>
      <img alt="Plant" src={ref} />
    </li>
  {/each}
</ul>
