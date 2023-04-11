<script lang="ts">
  import CheckRing from "$lib/assets/icons/checkRing.svg?component";

  export let text: string;
  export let bool: boolean;
</script>

<li class="float-root peer-checked:border-green">
  <input type="checkbox" id={text} bind:checked={bool} class=" hidden peer" />
  <label
    for={text}
    class="btn-primary inline-flex w-full p-4 border-2 rounded-2xl cursor-pointer justify-between"
  >
    <div>
      <h1 class="text-lg font-semibold capitalize">{text}</h1>
    </div>
    <div>
      {#if bool}
        <CheckRing class="fill-none" />
      {/if}
    </div>
  </label>
</li>
