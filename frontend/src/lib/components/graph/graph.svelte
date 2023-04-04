<script lang="ts">
  import { Line } from "./index";
  import Trash from "$assets/icons/trash.svg?component";
  import Humidity from "$assets/icons/humidity.svg?component";
  import Soil from "$assets/icons/soil.svg?component";
  import Sun from "$assets/icons/sun.svg?component";
  import Temperature from "$assets/icons/temperature.svg?component";

  export let data: any;

  import { data as test } from "./data";
  $: data = test;
  let graphType = ["temperature", "humidity"];
</script>

<div
  class="bg-base-100 w-fit max-w-md dark:bg-white/30 backdrop-blur-2xl rounded-2xl shadow-2xl p-2"
>
  <div class="absolute top-0 right-0 m-1">
    <Trash />
  </div>
  <br class="mt-1"/>
  <h1 class="flex justify-center text-2xl">Plant 1</h1>
  <div class="flex">
    <div class="h-full w-full min-w-max min-h-max">
      <Line {data} options={{ responsive: true}}/>
    </div>
    <div class="bg-green-500 shadow-2xl rounded-xl p-2 m-2">
      <Temperature />
      <Humidity />
      <Soil/>
      <Sun />
    </div>
  </div>
</div>
