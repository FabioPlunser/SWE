<script lang="ts">
  import {Chart as ChartJs, LineController, registerables} from "chart.js";
  import Chart from "./Chart.svelte";

  ChartJs.register(...registerables);
  ChartJs.register(LineController);
  export let chart:any = null; 
  let props:any; 
  let chartRef; 
  $: props = $$props;
</script>

<Chart bind:this={chartRef} bind:chart type="line" {...props} />