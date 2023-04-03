<script lang="ts">
  import Carousel from "./Carousel.svelte";
  import Camera from "$lib/assets/icons/Camera.svelte";
  export let data;
</script>

<div class="divide-y-4">
  <div class="flow-root">
    <div class="text-2xl py-4 float-left">
      <p>Room: {data.roomName}</p>
      <p>Plant: {data.plantName}</p>
    </div>
    <div>
      <button class="btn btn-primary float-right mt-8 mr-5">
        <!-- TODO: find a way to change fill color using 'fill' or 'dark:fill-white'
          does not seem to work even in Camara.svelte directlx-->
        <Camera classNames="w-8" />
      </button>
    </div>
  </div>
  <div class="py-8">
    <Carousel pictureRefs={data.pictureRefs} />
  </div>
</div>
