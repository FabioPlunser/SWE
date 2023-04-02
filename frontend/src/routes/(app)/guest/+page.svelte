<script lang="ts">
  export let data;
</script>

<h1>Images</h1>

<p>{data.plantName}</p>
<p>{data.roomName}</p>
{#each data.pictures as ref}
  <img alt="Plant image" src={ref} />
{/each}
<!--
-->
