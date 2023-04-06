<script lang="ts">
  import { onMount, afterUpdate, onDestroy } from "svelte";
  import { Chart as ChartJS } from "chart.js";

  let props = $$props;

  let canvasRef: HTMLCanvasElement;
  let chart: any = null;

  export let type: any;
  export let data = {
    datasets: [],
  };
  export let options: any = {};
  export let plugins: any = [];

  export let width: number = 500;
  export let height: number = 500;

  options = {
    plugins: {
      responsive: true,
      legend: {
        title: {
          display: false,
        },
        display: false,
      },
    },
    scales: {
      x: {
        ticks: {
          maxRotation: 90,
          minRotation: 15,
        },
      },
    },
  };
  onMount(() => {
    chart = new ChartJS(canvasRef, {
      type,
      data,
      options,
      plugins,
    });
  });

  afterUpdate(() => {
    if (!chart) return;
    chart.data = data;
    Object.assign(chart.options, options);
    chart.update();
  });
  onDestroy(() => {
    if (chart) chart.destroy();
    chart = null;
  });
</script>

<div class="relative w-full h-full m-auto">
  <canvas
    class="h-[{height}px] w-[{width}px]"
    bind:this={canvasRef}
    {...props}
  />
</div>
