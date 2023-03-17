<script lang="ts">
  import { onMount } from "svelte";
  
  export let name: string; 
  export let w: string; 
  export let h: string;
  export let shadow: boolean = false;
  let clazz: any;
	export { clazz as class };

  let icon: any;  
  onMount(async () => {
    
    icon = (await import(`../../assets/icons/${name}.svg?component`)).default;
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


<!-- <img src="{icon}" alt="{name}" width="{w}" height="{h}" class="dark:invert {shadow ? "drop-shadow-3xl" : ""} {css}" />
 -->

<svelte:component this="{icon}"  width="{w}" height="{h}" class="dark:invert {shadow ? "drop-shadow-3xl" : ""} {clazz}" />