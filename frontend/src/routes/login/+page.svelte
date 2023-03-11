<script lang="ts">
    import type { PageData } from './$types';
    export let data: PageData;
</script>

<section class="text-white">
<div class="">
<h1 class="flex justify-center text-4xl font-bold">Login</h1>
<div class="">
<div class="mx-auto form-control w-full max-w-xs">
<label class="label">
<span class="label-text">Username</span>
</label>
<input type="text" placeholder="Type here" class="input input-bordered w-full max-w-xs" />
</div>
<div class="mx-auto form-control w-full max-w-xs">
<label class="label">
<span class="label-text">Password</span>
</label>
<input type="text" placeholder="Type here" class="input input-bordered w-full max-w-xs" />
</div>
<div class="flex justify-center mt-4">
<button class="btn btn-primary">Login</button>
</div>
{data.prop}
</div>
</div>
</section>