<script lang="ts">
  import { Line } from "./index";
  export let data: any; 

  import { data as test} from "./data";
  $: data = test;
</script>

<div class="bg-base-100 dark:bg-white/30 backdrop-blur-2xl rounded-2xl shadow-2xl p-2">
  <Line {data} options={{ responsive: true }}/>
</div>