<script lang="ts">
  import { theme } from "$stores/themeStore";
  import { page } from "$app/stores";
  import { slide, fly } from "svelte/transition";
  import Mobile from "$helper/Mobile.svelte";
  import Desktop from "$helper/Desktop.svelte";

  let open = false;
</script>
