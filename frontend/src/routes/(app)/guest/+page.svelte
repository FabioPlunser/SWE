<script lang="ts">
  import Carousel from "./Carousel.svelte";
  export let data;
</script>

<h1>Images</h1>

<p>{data.plantName}</p>
<p>{data.roomName}</p>
<!-- TODO: create infinite scroll-->
<Carousel pictureRefs={data.pictureRefs} />
<!--
{#each data.pictureRefs as ref}
  <img alt="Plant" src={ref} />
{/each}-->
