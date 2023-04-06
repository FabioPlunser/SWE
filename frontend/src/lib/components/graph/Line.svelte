<script lang="ts">
  import {Chart as ChartJs, LineController, registerables} from "chart.js";
  import Chart from "./Chart.svelte";

  ChartJs.register(...registerables);
  ChartJs.register(LineController);
  export let chart:any = null; 
  export let width:number = 500;
  export let height:number = 500;
  let props:any; 
  let chartRef; 
  $: props = $$props;
</script>

<div class="w-full h-full">
  <Chart bind:width={width} bind:height={height} bind:this={chartRef} bind:chart type="line" {...props} />
</div>