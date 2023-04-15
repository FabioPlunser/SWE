<script lang="ts">
  import Input from "$components/ui/Input.svelte";

  export let data;
  $: console.log(data);
  $: console.log(data.accessPoints[0]);
  let entries = [
    "roomName",
    "scanActive",
    "connected",
    "deleted",
    "transferInterval",
    "unlocked",
  ];
</script>

<section class="mt-14">
  {#if data.accessPoints}
    {#each data.accessPoints as accessPoint, i}
      <div class="card bg-base-100 shadow-xl w-fit p-5">
        <div>
          <h1 class="flex justify-center text-2xl font-bold">
            AccessPoint: {accessPoint?.selfAssignedId.slice(0, 5)}
          </h1>

          {#each Object.entries(accessPoint) as [key, value]}
            {#if entries.includes(key)}
              <div class="flex">
                <label class="label">
                  <span class="label-text font-bold items-center">{key}:</span>
                </label>
                <h1 class="ml-2 flex items-center">{value}</h1>
              </div>
            {/if}
          {/each}
        </div>
        <div class="card-actions mt-10 mx-auto">
          <button class="btn btn-primary">Search Stations</button>
          <button class="btn btn-error">Delete</button>
        </div>
      </div>
    {/each}
  {/if}
</section>
