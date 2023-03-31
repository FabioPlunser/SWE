<script lang="ts">
  import { onMount, afterUpdate, onDestroy } from 'svelte';
  import { Chart as ChartJS } from "chart.js";
  
  let props = $$props;

  let canvasRef: HTMLCanvasElement;
  export let type:any; 
  export let data = {
    datasets: [],
  };
  export let options:any = {};
  export let plugins:any = [];
  let chart:any = null;
  
  onMount(()=>{
    chart = new ChartJS(canvasRef, {
      type, 
      data, 
      options, 
      plugins,
    })
  })

  afterUpdate(()=>{
    if(!chart) return; 
    chart.data = data;
    Object.assign(chart.options, options);
    chart.update();
  })
  onDestroy(()=>{
    if(chart)
      chart.destroy();
    chart = null;
  })
</script>

<canvas bind:this={canvasRef} {...props}/>