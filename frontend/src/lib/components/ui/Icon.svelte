<script lang="ts">
  import { onMount } from "svelte";
  
  export let name: string; 
  export let w: string; 
  export let h: string;
  export let shadow: boolean = false;
  export let css: string = "";

  let icon: any;  
  onMount(async () => {
    
    icon = (await import(`../../assets/icons/${name}.svg?url`)).default;
    console.log(icon);
  });

</script>

<!-- @component
  name: Icon
  description: Icon component
  props:
    name: string
    w: number
    h: number
-->


<img src="{icon}" alt="{name}" width="{w}" height="{h}" class="dark:invert {shadow ? "drop-shadow-3xl" : ""} {css}" />
